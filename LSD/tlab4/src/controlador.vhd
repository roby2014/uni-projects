LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY controlador IS
    PORT (
        START : IN STD_LOGIC;
        reset : IN STD_LOGIC;
        Mclk : IN STD_LOGIC;
        TC : IN STD_LOGIC;
        PL, CE, CLEAR, Rst, EA, ER : OUT STD_LOGIC
    );
END controlador;

ARCHITECTURE arq OF controlador IS

    COMPONENT controlador_registo
        PORT (
            D : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
            CLK : IN STD_LOGIC;
            Reset : IN STD_LOGIC;
            Q : OUT STD_LOGIC_VECTOR(1 DOWNTO 0)
        );
    END COMPONENT;

    COMPONENT controlador_ROM IS
        PORT (
            START, TC : IN STD_LOGIC;
            EP : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
            PL, CE, CLEAR, Rst, EA, ER : OUT STD_LOGIC;
            ES : OUT STD_LOGIC_VECTOR(1 DOWNTO 0)
        );
    END COMPONENT;

    SIGNAL s_d, s_q : STD_LOGIC_VECTOR(1 DOWNTO 0);

BEGIN

    u_controlador_registo : controlador_registo PORT MAP(
        D => s_d,
        CLK => Mclk,
        Reset => reset,
        Q => s_q
    );

    u_controlador_ROM : controlador_ROM PORT MAP(
        START => START,
        TC => TC,
        PL => PL,
        CE => CE,
        CLEAR => CLEAR,
        Rst => Rst,
        EA => EA,
        ER => ER,
        EP => s_q,
        ES => s_d
    );

END arq;