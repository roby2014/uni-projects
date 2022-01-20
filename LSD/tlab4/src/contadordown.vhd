LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY contadordown IS
    PORT (
        m : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        PL, CE, CLK, reset : IN STD_LOGIC;
        TC : OUT STD_LOGIC
    );
END contadordown;

ARCHITECTURE arq OF contadordown IS

    COMPONENT mux
        PORT (
            A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            sel : IN STD_LOGIC;

            F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
        );
    END COMPONENT;

    COMPONENT contadordown_registo
        PORT (
            CLK : IN STD_LOGIC;
            CE : IN STD_LOGIC;
            M : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
            reset : IN STD_LOGIC
        );
    END COMPONENT;

    COMPONENT adder4bits
        PORT (
            A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            Cin : IN STD_LOGIC;
            S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
            Cout : OUT STD_LOGIC
        );
    END COMPONENT;

    SIGNAL sSom, sM, sQ : STD_LOGIC_VECTOR(3 DOWNTO 0);

BEGIN
    u_mux : mux PORT MAP(
        A => m,
        B => sSom,
        sel => PL,
        F => sM
    );

    u_cd_registo : contadordown_registo PORT MAP(
        CE => CE,
        CLK => CLK,
        M => sM,
        Q => sQ,
        reset => reset
    );

    u_somador : adder4bits PORT MAP(
        A => sQ,
        B => "1111", -- m ?
        Cin => '0',
        Cout => OPEN,
        S => sSom
    );

    TC <= NOT(sQ(0)) AND NOT(sQ(1)) AND NOT(sQ(2)) AND NOT(sQ(3));
END arq;