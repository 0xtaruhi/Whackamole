#include <QFile>
#include <QStringView>

#include "CoeReader.h"

CoeReader::CoeReader(QObject *parent) : QObject(parent) {}

CoeReader::~CoeReader() {}

auto CoeReader::open(const QString &path) -> void {
  QFile file(path);
  if (!file.open(QIODevice::ReadOnly | QIODevice::Text)) {
    return;
  }

  data_.clear();
  auto radix = 0;

  auto line = QString(file.readLine().trimmed());
  if (line.startsWith("memory_initialization_radix")) {
    radix = line.split('=').last().remove(';').toInt();
    if (!(radix == 2 || radix == 10 || radix == 16)) {
      throw std::runtime_error("Invalid radix");
    }
  }
  line = QString(file.readLine());
  if (!line.startsWith("memory_initialization_vector")) {
    throw std::runtime_error("Invalid file format");
  }

  while (!file.atEnd()) {
    line = QString(file.readLine().trimmed());
    if (line.isEmpty()) {
      continue;
    }
    line = line.split("//").first();
    if (line.startsWith(';')) {
      break;
    }
    auto tokens = line.split(',');
    for (auto token : tokens) {
      bool ok = false;
      auto number = token.trimmed().toInt(&ok, radix);
      if (ok) {
        data_.push_back(number);
      }
    }
  }
}
