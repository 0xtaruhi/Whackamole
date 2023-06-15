#pragma once
#ifndef COE_READER_H
#define COE_READER_H

#include <QObject>
#include <QVector>

class Memory;

class CoeReader final : public QObject {
  Q_OBJECT

  friend class Memory;

public:
  CoeReader(QObject* parent = nullptr);
  ~CoeReader();

  auto open(const QString& path) -> void;

private:
  QVector<int32_t> data_;
};

#endif // COE_READER_H
