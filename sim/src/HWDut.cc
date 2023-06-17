/*
 * File: HWDut.cc
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Friday, 16th June 2023 7:04:08 pm
 * Last Modified: Saturday, 17th June 2023 10:49:14 am
 * Copyright: 2023 - 2023 Fudan University
 */
#include <QDebug>
#include <cstdint>
#include <exception>
#include <qsize.h>
#include <qtypes.h>

#include "CoeReader.h"
#include "HWDut.h"
#include "Memory.h"
#include "config.h"

HWDut::HWDut(QObject *parent) : HWDut(false, parent) {}

HWDut::HWDut(bool trace_enabled, QObject *parent) : QObject(parent) {
  trace_enabled_ = trace_enabled;
  image1_ = QImage(640, 480, QImage::Format_RGB888);
  image2_ = QImage(640, 480, QImage::Format_RGB888);

  key_states_ =
      QVector<QVector<KeyState>>(4, QVector<KeyState>(4, KeyState::Released));

  initVerilator();
  initMemories();

  timer_.setInterval(1000 / 5);
  connect(&timer_, &QTimer::timeout, this, &HWDut::onEmitNewFrame);
  timer_.start();
  cur_write_image_ = &image1_;
}

HWDut::~HWDut() {
  top_->final();
  if (trace_enabled_) {
    trace_->close();
  }
}

auto HWDut::initVerilator() -> void {
  Verilated::traceEverOn(true);

  top_ = std::make_unique<VGameTop>(&context_);
  trace_ = std::make_unique<VerilatedVcdC>();

  if (trace_enabled_) {
    top_->trace(trace_.get(), 99);
    trace_->open((std::string(top_->name()) + ".vcd").c_str());
  }

  top_->io_start = 0;
  top_->resetn = 0;
  for (int i = 0; i < 10; i++) {
    tick();
  }
  top_->resetn = 1;
  tick();
}

auto HWDut::initMemories() -> void {
  auto number_coe_reader_ = CoeReader();
  auto mole_hide_coe_reader_ = CoeReader();
  auto mole_show_coe_reader = CoeReader();

  try {
    number_coe_reader_.open(RES_DIR "/coe/numbers.coe");
    mole_hide_coe_reader_.open(RES_DIR "/coe/mole-hide.coe");
    mole_show_coe_reader.open(RES_DIR "/coe/mole-show.coe");
  } catch (std::exception &e) {
    qDebug() << e.what();
    exit(1);
  }

  numbers_mem_ = new Memory(std::move(number_coe_reader_), this);
  mole_hide_mem_ = new Memory(std::move(mole_hide_coe_reader_), this);
  mole_show_mem_ = new Memory(std::move(mole_show_coe_reader), this);
}

auto HWDut::readMemory(int32_t addr) -> int32_t {
  auto addr_tag = (addr >> 14) & 0xf;

  auto memAddrResize = [](int32_t addr, int width) -> int32_t {
    return addr & ((1 << width) - 1);
  };

  if (addr_tag == 0x00) {
    return numbers_mem_->read(memAddrResize(addr, numbers_mem_->addrWidth()));
  } else if (addr_tag == 0x01) {
    return mole_hide_mem_->read(
        memAddrResize(addr, mole_hide_mem_->addrWidth()));
  } else if (addr_tag == 0x02) {
    return mole_show_mem_->read(
        memAddrResize(addr, mole_show_mem_->addrWidth()));
  } else {
    qDebug() << "Invalid memory access: " << addr;
    qDebug() << "Tag: " << addr_tag;
    return 0;
  }
}

auto HWDut::onEmitNewFrame() -> void {
  int h_counter = 0;
  int v_counter = 0;

  while (!(h_counter == 0 && v_counter == 525)) {
    auto mem_addr = top_->io_memAddr;
    tick();
    top_->io_memData = readMemory(mem_addr);

    if (h_counter < 640 && v_counter < 480) [[likely]] {
      curWriteImage()->setPixel(
          h_counter, v_counter,
          qRgb(top_->io_rgb_0, top_->io_rgb_1, top_->io_rgb_2));
    }

    if (h_counter == 799) {
      h_counter = 0;
      v_counter++;
    } else {
      h_counter++;
    }
  }

  emit newFrame(curWriteImage());
  swapImages();
}

auto HWDut::onKeyPressed(QPair<int, int> position) -> void {
  key_states_[position.first][position.second] = KeyState::Pressed;
  top_->io_keyPress = true;
  top_->io_keyIndex = position.first * 4 + position.second;
}

auto HWDut::onKeyReleased(QPair<int, int> position) -> void {
  key_states_[position.first][position.second] = KeyState::Released;
  top_->io_keyPress = false;
}

auto HWDut::onStart() -> void {
  top_->io_start = 1;
}

auto HWDut::onReset() -> void {
  top_->io_start = 0;
  top_->resetn = 0;
  for (int i = 0; i < 10; i++) {
    tick();
  }
  top_->resetn = 1;
  tick();
}

auto HWDut::tick() -> void {
  top_->clk = 0;
  top_->eval();
  if (trace_enabled_) [[unlikely]] {
    trace_->dump(10 * tick_count_ + 0);
  }
  top_->clk = 1;
  top_->eval();
  if (trace_enabled_) [[unlikely]] {
    trace_->dump(10 * tick_count_ + 5);
  }
  tick_count_++;
}
