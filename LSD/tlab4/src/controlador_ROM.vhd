LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY controlador_ROM IS
    PORT (
        START, TC : IN STD_LOGIC;
        EP : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
        PL, CE, CLEAR, Rst, EA, ER : OUT STD_LOGIC;
        ES : OUT STD_LOGIC_VECTOR(1 DOWNTO 0)
    );
END controlador_ROM;

ARCHITECTURE logicFunction OF controlador_ROM IS

    SIGNAL data : STD_LOGIC_VECTOR(7 DOWNTO 0);
    SIGNAL address : STD_LOGIC_VECTOR(3 DOWNTO 0);

BEGIN

    address <= EP & START & TC;

    ES <= data(7 DOWNTO 6);
    PL <= data(5);
    Rst <= data(4);
    CLEAR <= data(3);
    CE <= data(2);
    EA <= data(1);
    ER <= data(0);

    data <= "01111101" WHEN address = "0000" ELSE
        "01111101" WHEN address = "0001" ELSE
        "00111101" WHEN address = "0010" ELSE
        "00111101" WHEN address = "0011" ELSE
        "01000110" WHEN address = "0100" ELSE
        "10000000" WHEN address = "0101" ELSE
        "01000110" WHEN address = "0110" ELSE
        "10000000" WHEN address = "0111" ELSE
        "10000000" WHEN address = "1000" ELSE
        "10000000" WHEN address = "1001" ELSE
        "00000000" WHEN address = "1010" ELSE
        "00000000";

END logicFunction;