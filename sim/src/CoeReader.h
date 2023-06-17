/*
 * File: CoeReader.h
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Thursday, 15th June 2023 6:56:52 pm
 * Last Modified: Saturday, 17th June 2023 10:48:53 am
 * Copyright: 2023 - 2023 Fudan University
 */
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
