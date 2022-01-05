-- mux lab 5

LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY mux IS
    PORT (
        A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        PL : IN STD_LOGIC; -- seletor parallel load

        F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
    );
END mux;

ARCHITECTURE arq OF mux IS
BEGIN
    F(0) <= (PL AND A(0)) OR (NOT(PL) AND B(0));
    F(1) <= (PL AND A(1)) OR (NOT(PL) AND B(1));
    F(2) <= (PL AND A(2)) OR (NOT(PL) AND B(2));
    F(3) <= (PL AND A(3)) OR (NOT(PL) AND B(3));
END arq;