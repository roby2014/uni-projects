LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY somador IS
    PORT (
        A : IN STD_LOGIC_VECTOR(6 DOWNTO 0);
        B : IN STD_LOGIC_VECTOR(6 DOWNTO 0);
        Cin : IN STD_LOGIC;
        S : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
        Cout : OUT STD_LOGIC
    );
END somador;

ARCHITECTURE arq OF somador IS

    COMPONENT fulladder
        PORT (
            A : IN STD_LOGIC;
            B : IN STD_LOGIC;
            Cin : IN STD_LOGIC;
            S : OUT STD_LOGIC;
            Cout : OUT STD_LOGIC
        );
    END COMPONENT;
    SIGNAL c1, c2, c3, c4, c5, c6 : STD_LOGIC;

BEGIN

    u_fulladder0 : fulladder PORT MAP(
        A => A(0),
        B => B(0),
        Cin => Cin,
        S => S(0),
        Cout => c1);

    u_fulladder1 : fulladder PORT MAP(
        A => A(1),
        B => B(1),
        Cin => c1,
        S => S(1),
        Cout => c2);

    u_fulladder2 : fulladder PORT MAP(
        A => A(2),
        B => B(2),
        Cin => c2,
        S => S(2),
        Cout => c3);

    u_fulladder3 : fulladder PORT MAP(
        A => A(3),
        B => B(3),
        Cin => c3,
        S => S(3),
        Cout => c4);

    u_fulladder4 : fulladder PORT MAP(
        A => A(4),
        B => B(4),
        Cin => c4,
        S => S(4),
        Cout => c5);

    u_fulladder5 : fulladder PORT MAP(
        A => A(5),
        B => B(5),
        Cin => c5,
        S => S(5),
        Cout => c6);

    u_fulladder6 : fulladder PORT MAP(
        A => A(6),
        B => B(6),
        Cin => c6,
        S => S(6),
        Cout => Cout);

END arq;