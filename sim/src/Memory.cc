/*
 * File: Memory.cc
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Thursday, 15th June 2023 6:56:52 pm
 * Last Modified: Saturday, 17th June 2023 10:49:44 am
 * Copyright: 2023 - 2023 Fudan University
 */
#include "Memory.h"
#include "CoeReader.h"
#include <cstdint>

Memory::Memory(QObject *parent) : QObject(parent) {}

Memory::Memory(CoeReader &&coe_reader, QObject *parent)
    : QObject(parent), data_(std::move(coe_reader.data_)) {
  addr_width_ = std::ceil(std::log2(data_.size()));
}

Memory::~Memory() {}

auto Memory::loadFromCoeReader(CoeReader &&coe_reader) -> void {
  data_ = std::move(coe_reader.data_);
  addr_width_ = std::ceil(std::log2(data_.size()));
}

auto Memory::read(int32_t addr) -> int32_t {
  addr = addr & ((1 << addr_width_) - 1);
  if (addr < 0 || addr >= data_.size()) {
    // throw std::runtime_error("Invalid address");
    return 0;
   }
  return data_[addr];
}
