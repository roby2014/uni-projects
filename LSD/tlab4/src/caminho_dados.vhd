LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY caminho_dados IS
    PORT (
        Mclk, reset, PL, CE, EA, ER, Rst, clear : IN STD_LOGIC;
        M, md : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        TC : OUT STD_LOGIC;
        HEX0, HEX1, HEX2, HEX3, HEX4, HEX5 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
        P : OUT STD_LOGIC_VECTOR(6 DOWNTO 0)
    );
END caminho_dados;

ARCHITECTURE arq OF caminho_dados IS

    COMPONENT contadordown
        PORT (
            m : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            PL, CE, CLK, reset : IN STD_LOGIC;
            TC : OUT STD_LOGIC
        );
    END COMPONENT;

    COMPONENT registo
        PORT (
            ER, CLK : IN STD_LOGIC;
            D : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            Q : OUT STD_LOGIC_VECTOR(6 DOWNTO 0)
        );
    END COMPONENT;

    COMPONENT somador IS
        PORT (
            A : IN STD_LOGIC_VECTOR(6 DOWNTO 0);
            B : IN STD_LOGIC_VECTOR(6 DOWNTO 0);
            Cin : IN STD_LOGIC;
            S : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
            Cout : OUT STD_LOGIC
        );
    END COMPONENT;

    COMPONENT acc IS
        PORT (
            EA, CLK, Rst : IN STD_LOGIC;
            D : IN STD_LOGIC_VECTOR(6 DOWNTO 0);
            Q : OUT STD_LOGIC_VECTOR(6 DOWNTO 0)
        );
    END COMPONENT;

    COMPONENT decoderHex IS
        PORT (
            A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            P : IN STD_LOGIC_VECTOR(6 DOWNTO 0);
            clear : IN STD_LOGIC;
            HEX0, HEX1, HEX2, HEX3, HEX4, HEX5 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0)
        );
    END COMPONENT;

    SIGNAL sA, sD, sQ : STD_LOGIC_VECTOR(6 DOWNTO 0);

BEGIN

    u_contadordown : contadordown PORT MAP(
        m => md,
        PL => PL,
        reset => reset,
        CE => CE,
        CLK => Mclk,
        TC => TC
    );

    u_registo : registo PORT MAP(
        CLK => Mclk,
        ER => ER,
        D => M,
        Q => sA
    );

    u_somador : somador PORT MAP(
        A => sA,
        B => sQ,
        S => sD,
        Cin => '0',
        Cout => OPEN
    );

    u_acc : acc PORT MAP(
        CLK => Mclk,
        EA => EA,
        D => sD,
        Q => sQ,
        Rst => Rst
    );

    UdecoderHex : decoderHex PORT MAP(
        A => M,
        B => md,
        P => sQ,
        clear => clear,
        HEX0 => HEX0,
        HEX1 => HEX1,
        HEX2 => HEX2,
        HEX3 => HEX3,
        HEX4 => HEX4,
        HEX5 => HEX5
    );

    P <= sQ;

END arq;