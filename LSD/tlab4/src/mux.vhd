LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY mux IS
    PORT (
        A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        sel : IN STD_LOGIC;

        F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
    );
END mux;

ARCHITECTURE arq OF mux IS
BEGIN
    F(0) <= (sel AND A(0)) OR (NOT(sel) AND B(0));
    F(1) <= (sel AND A(1)) OR (NOT(sel) AND B(1));
    F(2) <= (sel AND A(2)) OR (NOT(sel) AND B(2));
    F(3) <= (sel AND A(3)) OR (NOT(sel) AND B(3));
END arq;