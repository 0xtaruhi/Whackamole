#ifndef HW_DUT_H
#define HW_DUT_H

#include <QImage>
#include <QObject>
#include <QRgb>
#include <QTimer>
#include <qimage.h>

/// @brief The HWDut class is a hardware device under test. It is a wrapper
/// of the verilator model. It is running in a separate thread.
class HWDut final : public QObject {
  Q_OBJECT

public:
  HWDut(QObject *parent = nullptr);
  ~HWDut() = default;

private slots:
  /// @brief Emit a signal to notify the main thread that a new frame is
  /// available. This slot is connected to the timer's timeout signal.
  auto onEmitNewFrame() -> void;

signals:
  /// @brief Notify the main thread that a new frame is available.
  /// @param image The image to be displayed.
  auto newFrame(QImage *image) -> void;

private:
  auto curWriteImage() -> QImage * { return cur_write_image_; }
  auto curReadImage() -> QImage * {
    return cur_write_image_ == &image1_ ? &image2_ : &image1_;
  }

  /// @brief Swap the image to be written to. Here we use double buffering.
  auto swapImages() -> void { cur_write_image_ = curReadImage(); }

private:
  QTimer timer_; //< The timer to trigger the new frame signal.
  QImage image1_;
  QImage image2_;
  QImage *cur_write_image_;
};

#endif // HW_DUT_H
