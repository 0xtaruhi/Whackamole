#ifndef HW_DUT_H
#define HW_DUT_H

#include <QObject>
#include <QTimer>
#include <QRgb>

class HWDut final : public QObject {
  Q_OBJECT

public:
  HWDut(QObject *parent = nullptr);
  ~HWDut() = default;

  auto nextCycle() -> void;

public slots:
  auto onNextRow(int rows = 1) -> void;
  auto onNextFrame() -> void;

signals:
  void receiveRowData(const QVector<QRgb> &vga_row);
  void receiveFrameData(const QVector<QVector<QRgb>>& vga_frame);
  void newFrame();

private:
  QTimer timer_;
};

#endif // HW_DUT_H
