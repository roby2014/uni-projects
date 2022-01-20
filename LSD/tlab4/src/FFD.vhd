LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY FFD IS
    PORT (
        CLK : IN STD_LOGIC;
        RESET : IN STD_LOGIC;
        SET : IN STD_LOGIC;
        D : IN STD_LOGIC;
        EN : IN STD_LOGIC;
        Q : OUT STD_LOGIC
    );
END FFD;

ARCHITECTURE arq OF FFD IS

BEGIN
    Q <= '0' WHEN RESET = '1' ELSE
        '1' WHEN SET = '1' ELSE
        D WHEN rising_edge(clk) AND EN = '1';
END arq;