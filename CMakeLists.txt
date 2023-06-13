cmake_minimum_required(VERSION 3.15)
project(whackamole VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
# set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -Wpedantic -Werror")

# Software simulation of the game
add_subdirectory(sim)
