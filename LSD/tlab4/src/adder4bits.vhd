LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY adder4bits IS
    PORT (
        A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        Cin : IN STD_LOGIC;
        S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
        Cout : OUT STD_LOGIC
    );
END adder4bits;

ARCHITECTURE arq OF adder4bits IS

    COMPONENT fulladder
        PORT (
            A : IN STD_LOGIC;
            B : IN STD_LOGIC;
            Cin : IN STD_LOGIC;
            S : OUT STD_LOGIC;
            Cout : OUT STD_LOGIC
        );
    END COMPONENT;
    SIGNAL C1, C2, C3 : STD_LOGIC;

BEGIN

    u_fulladder0 : fulladder PORT MAP(
        A => A(0),
        B => B(0),
        Cin => Cin,
        S => S(0),
        Cout => C1);

    u_fulladder1 : fulladder PORT MAP(
        A => A(1),
        B => B(1),
        Cin => C1,
        S => S(1),
        Cout => C2);

    u_fulladder2 : fulladder PORT MAP(
        A => A(2),
        B => B(2),
        Cin => C2,
        S => S(2),
        Cout => C3);

    u_fulladder3 : fulladder PORT MAP(
        A => A(3),
        B => B(3),
        Cin => C3,
        S => S(3),
        Cout => Cout);

END arq;