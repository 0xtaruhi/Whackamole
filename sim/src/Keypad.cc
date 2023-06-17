/*
 * File: Keypad.cc
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Wednesday, 14th June 2023 9:53:59 pm
 * Last Modified: Saturday, 17th June 2023 10:49:23 am
 * Copyright: 2023 - 2023 Fudan University
 */
#include <QDebug>
#include <QEvent>
#include <QMouseEvent>
#include <QPainter>

#include "Keypad.h"

Key::Key(QWidget *parent) : QWidget(parent) {}

void Key::paintEvent(QPaintEvent *event) {
  QPainter painter(this);
  painter.setRenderHint(QPainter::Antialiasing);

  painter.setPen(Qt::NoPen);
  painter.setBrush(is_pressed_ ? Qt::black : Qt::darkGray);
  painter.drawEllipse(rect());

  QWidget::paintEvent(event);
}

void Key::mousePressEvent(QMouseEvent *event) {
  if (event->button() == Qt::LeftButton) {
    is_pressed_ = true;
    update();
    emit clicked();
  }
}

void Key::mouseReleaseEvent(QMouseEvent *event) {
  if (event->button() == Qt::LeftButton) {
    is_pressed_ = false;
    update();
    emit released();
  }
}

Keypad::Keypad(int row_num, int col_num, QWidget *parent)
    : QWidget(parent), keypad_size_(col_num, row_num) {
  setFixedSize(keypad_size_ * 50);

  for (int row = 0; row < keypad_size_.height(); ++row) {
    for (int col = 0; col < keypad_size_.width(); ++col) {
      auto key = new Key(this);
      key->setFixedSize(40, 40);
      key->move(col * 50 + 5, row * 50 + 5);

      key_positions_.insert(key, {row, col});
      keys_.push_back(key);

      connect(key, &Key::clicked, this,
              [this, key] { emit clicked(key_positions_.value(key)); });

      connect(key, &Key::released, this,
              [this, key] { emit released(key_positions_.value(key)); });
    }
  }
}
