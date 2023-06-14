#include <QDebug>

#include "HWDut.h"

HWDut::HWDut(QObject *parent) : HWDut(false, parent) {}

HWDut::HWDut(bool trace_enabled, QObject *parent) : QObject(parent) {
  trace_enabled_ = trace_enabled;
  image1_ = QImage(640, 480, QImage::Format_RGB888);
  image2_ = QImage(640, 480, QImage::Format_RGB888);

  key_states_ =
      QVector<QVector<KeyState>>(4, QVector<KeyState>(4, KeyState::Released));

  initVerilator();

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

  top_->resetn = 0;
  for (int i = 0; i < 10; i++) {
    tick();
  }
  top_->resetn = 1;
  tick();
}

auto HWDut::onEmitNewFrame() -> void {
  int h_counter = 0;
  int v_counter = 0;

  while (!(h_counter == 0 && v_counter == 525)) {
    tick();
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
