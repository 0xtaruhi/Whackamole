/*
 * File: Common.h
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Monday, 12th June 2023 3:30:24 pm
 * Last Modified: Saturday, 17th June 2023 10:48:58 am
 * Copyright: 2023 - 2023 Fudan University
 */
#ifndef COMMON_H
#define COMMON_H

#include <stdint.h>
#include <type_traits>

using u8 = uint8_t;
using u16 = uint16_t;
using u32 = uint32_t;

template <typename T, typename std::enable_if_t<std::is_integral_v<T>, int> = 0>
constexpr auto getBit(T n, std::size_t bit) noexcept -> bool {
  return (n >> bit) & 1;
}

#endif // COMMON_H
