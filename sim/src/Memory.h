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
