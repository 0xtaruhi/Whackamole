#pragma once
#ifndef GRAPHICS_SIGNALS_H
#define GRAPHICS_SIGNALS_H

#include <array>

#include "Common.h"

namespace graphics {

struct TmdsSignals;
struct VgaSignals;

struct TmdsSignals {
  u16 blue;
  u16 green;
  u16 red;

  constexpr auto toVga() const noexcept -> VgaSignals;
};

struct VgaSignals {
  u8 blue;
  u8 green;
  u8 red;
  bool hsync;
  bool vsync;
};

inline constexpr auto tmdsToVga(TmdsSignals tmds [[maybe_unused]])
    -> VgaSignals {
  auto vga = VgaSignals{};

  auto simpleDecode = [](u16 tmds) -> u16 {
    u16 result = 0;
    u8 low8bit = 0;

    if (tmds & 0x100) {
      tmds = tmds ^ 0xff;
    }
    if (tmds & 0x80) {
      low8bit = static_cast<u8>(tmds ^ (tmds << 1));
    } else {
      low8bit = static_cast<u8>(~(tmds ^ ((tmds << 1) | 0x1)));
    }
    result = (tmds & 0x300) | low8bit;
    return result;
  };

  std::array<u16, 3> tmdsSignals
      [[maybe_unused]] = {simpleDecode(tmds.blue), simpleDecode(tmds.green),
                          simpleDecode(tmds.red)};

  return vga;
}

constexpr auto TmdsSignals::toVga() const noexcept -> VgaSignals {
  return tmdsToVga(*this);
}

} // namespace graphics

#endif // GRAPHICS_SIGNALS_H
