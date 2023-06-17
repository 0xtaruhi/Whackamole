/*
 * File: Memory.h
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Thursday, 15th June 2023 6:56:52 pm
 * Last Modified: Saturday, 17th June 2023 10:49:48 am
 * Copyright: 2023 - 2023 Fudan University
 */
#ifndef MEMORY_H
#define MEMORY_H

#include <QObject>

class CoeReader;

class Memory final : public QObject {
  Q_OBJECT

public:
  Memory(QObject *parent = nullptr);
  Memory(CoeReader &&coe_reader, QObject *parent = nullptr);

  ~Memory();

  auto loadFromCoeReader(CoeReader &&coe_reader) -> void;
  auto read(int32_t addr) -> int32_t;

  auto depth() const noexcept { return data_.size(); }
  auto addrWidth() const noexcept { return addr_width_; }

private:
  QVector<int32_t> data_;
  int addr_width_ = 0;
};

#endif // MEMORY_H
