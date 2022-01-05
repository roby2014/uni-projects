LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY fulladder IS
    PORT (
        A : IN STD_LOGIC;
        B : IN STD_LOGIC;
        Cin : IN STD_LOGIC; -- Cout anterior

        S : OUT STD_LOGIC;
        Cout : OUT STD_LOGIC
    );
END fulladder;

ARCHITECTURE arq OF fulladder IS
BEGIN
    S <= (A XOR B) XOR Cin;
    Cout <= (A AND B) OR (Cin AND (A XOR B));
END arq;