/*
 * File: main.cc
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Monday, 12th June 2023 3:12:41 pm
 * Last Modified: Saturday, 17th June 2023 10:49:31 am
 * Copyright: 2023 - 2023 Fudan University
 */
#include <QApplication>

#include "MainWindow.h"

int main(int argc, char **argv) {
  QApplication app(argc, argv);
  app.setStyle("fusion");

  MainWindow window;
  window.show();
  
  return app.exec();
}
