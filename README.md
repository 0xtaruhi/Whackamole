# Whackamole

---
**THIS PROJECT IS STILL UNDER DEVELOPMENT**
**PART OF THE DOCUMENTATION MAY BE INCOMPLETE OR OUTDATED**

---
## Table of Contents

- [Whackamole](#whackamole)
  - [Table of Contents](#table-of-contents)
  - [About ](#about-)
  - [Getting Started ](#getting-started-)
    - [Prerequisites](#prerequisites)
    - [Simulation](#simulation)
    - [Validation](#validation)

## About <a name="about"></a>

The Whackamole project is a Whack-a-Mole game implemented using SpinalHDL for RTL (Register Transfer Level) development, Verilator for simulation, and Qt for visualization. This project serves as the final course project for ASIC2023.

## Getting Started <a name="getting-started"></a>

These instructions will guide you through setting up the project on your local machine for development and testing purposes. 

### Prerequisites

Before you can use the software, you need to install the following dependencies:

- SpinalHDL
- Verilator
- Qt6

Please follow the installation instructions provided by the respective projects to install these dependencies on your system.

### Simulation

To run the simulation, follow these steps:

1. Clone the repository:

   ```
   git clone https://github.com/0xtaruhi/whackamole.git
   ```

2. Change into the project directory:

   ```
   cd whackamole
   ```

3. Build the project:

   ```
   mkdir build
   cmake -GNinja ..
   ninja
   ```

4. Run the simulation:

   ```
   sim/sim
   ```

   This will start the Whack-a-Mole simulation.

### Validation


We will use Procise as the EDA (Electronic Design Automation) tool for FPGA development to generate the bitstream and download it onto the FPGA development board. Currently, this part is still under development.
