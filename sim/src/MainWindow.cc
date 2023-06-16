#include <QGraphicsEffect>
#include <QLayout>
#include <qgraphicseffect.h>
#include <qpushbutton.h>
#include <qtimer.h>

#include "Canvas.h"
#include "HWDut.h"
#include "Keypad.h"
#include "MainWindow.h"

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent) {
  initMembers();
  initLayout();
}

MainWindow::~MainWindow() {
  hw_dut_thread_->quit();
  hw_dut_thread_->wait();
}

auto MainWindow::initMembers() -> void {
  canvas_ = new Canvas(640, 480, this);
  keypad_ = new Keypad(4, 4, this);
  hw_dut_ = new HWDut();
  reset_button_ = new QPushButton("Reset", this);
  start_button_ = new QPushButton("Start", this);

  frame_rate_label_ = new QLabel(this);

  second_timer_ = new QTimer(this);
  second_timer_->setInterval(1000);
  second_timer_->start();

  connect(second_timer_, &QTimer::timeout, this,
          &MainWindow::onUpdateFrameRate);
  onUpdateFrameRate();

  hw_dut_thread_ = new QThread(this);
  hw_dut_thread_->start();

  hw_dut_->moveToThread(hw_dut_thread_);

  QGraphicsDropShadowEffect *effect = new QGraphicsDropShadowEffect(this);
  effect->setBlurRadius(10);
  effect->setOffset(0);
  effect->setColor(Qt::black);
  canvas_->setGraphicsEffect(effect);

  connect(hw_dut_, &HWDut::newFrame, canvas_, &Canvas::onUpdateImage);
  connect(hw_dut_, &HWDut::newFrame, this, &MainWindow::onNewFrame);
  connect(keypad_, &Keypad::clicked, hw_dut_, &HWDut::onKeyPressed);
  connect(keypad_, &Keypad::released, hw_dut_, &HWDut::onKeyReleased);
  connect(reset_button_, &QPushButton::clicked, hw_dut_, &HWDut::onReset);
  connect(start_button_, &QPushButton::clicked, hw_dut_, &HWDut::onStart);
}

auto MainWindow::initLayout() -> void {
  auto *centralWidget = new QWidget(this);
  auto *layout = new QVBoxLayout(centralWidget);
  layout->addWidget(canvas_);
  layout->addWidget(keypad_);

  // Add buttons
  auto *buttons_layout = new QHBoxLayout();
  buttons_layout->addWidget(reset_button_);
  buttons_layout->addWidget(start_button_);
  layout->addLayout(buttons_layout);

  // Add frame rate label
  layout->addWidget(frame_rate_label_);

  layout->setAlignment(keypad_, Qt::AlignHCenter);

  setCentralWidget(centralWidget);

  setWindowTitle("Whackmole Emulator");
  this->setFixedSize(centralWidget->sizeHint());
}

void MainWindow::onUpdateFrameRate() {
  frame_rate_label_->setText(QString("Frame Rate: %1").arg(frame_count_));
  frame_count_ = 0;
}
