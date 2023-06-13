# copy all files in rtl/verilog to sim/verilog
file(GLOB RTL_FILES ${CMAKE_SOURCE_DIR}/rtl/verilog)
file(COPY ${RTL_FILES} DESTINATION ${CMAKE_SOURCE_DIR}/sim/verilog)

# find Qt6
find_package(Qt6 COMPONENTS Core Gui Widgets REQUIRED)

# find verilator
find_package(verilator HINTS $ENV{VERILATOR_ROOT})

# Qt
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

set(SIM_SRCS
    src/main.cc
    src/MainWindow.h
    src/MainWindow.cc
    src/Canvas.h
    src/Canvas.cc
    src/Keypad.h
    src/Keypad.cc
    src/HWDut.h
    src/HWDut.cc
)

qt_add_executable(sim
    MANUAL_FINALIZATION
    ${SIM_SRCS}
)

set_target_properties(sim PROPERTIES
    MACOSX_BUNDLE_GUI_IDENTIFIER taruhi.whackmole.com
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

target_include_directories(sim PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/src)
target_link_libraries(sim PRIVATE Qt6::Widgets)

qt_finalize_executable(sim)
