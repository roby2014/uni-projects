LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY tlab4 IS
    PORT (
        start, reset : IN STD_LOGIC;
        Mclk : IN STD_LOGIC;
        M, md : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        P : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
        HEX0, HEX1, HEX2, HEX3, HEX4, HEX5 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0)
    );
END tlab4;

ARCHITECTURE arq OF tlab4 IS

    COMPONENT caminho_dados
        PORT (
            Mclk, reset, PL, CE, EA, ER, RST, clear : IN STD_LOGIC;
            M, md : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            TC : OUT STD_LOGIC;
            HEX0, HEX1, HEX2, HEX3, HEX4, HEX5 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
            P : OUT STD_LOGIC_VECTOR(6 DOWNTO 0)
        );
    END COMPONENT;

    COMPONENT controlador IS
        PORT (
            START : IN STD_LOGIC;
            reset : IN STD_LOGIC;
            Mclk : IN STD_LOGIC;
            TC : IN STD_LOGIC;
            PL, CE, CLEAR, RST, EA, Er : OUT STD_LOGIC
        );
    END COMPONENT;

    -- sinais entre controlo e caminho dados
    SIGNAL s_pl, s_ce, s_ea, s_er, s_tc, s_rst, s_clear : STD_LOGIC;

BEGIN

    u_caminho_dados : caminho_dados PORT MAP(
        M => M,
        md => md,
        PL => s_pl,
        CE => s_ce,
        reset => reset,
        EA => s_ea,
        ER => s_er,
        TC => s_tc,
        RST => s_rst,
        clear => s_clear,
        Mclk => Mclk,
        HEX0 => HEX0,
        HEX1 => HEX1,
        HEX2 => HEX2,
        HEX3 => HEX3,
        HEX4 => HEX4,
        HEX5 => HEX5,
        P => P
    );

    u_controlador : controlador PORT MAP(
        START => start, --
        reset => reset, --
        Mclk => Mclk,
        TC => s_tc,
        PL => s_pl,
        CE => s_ce,
        CLEAR => s_clear,
        RST => s_rst,
        EA => s_ea,
        Er => s_er
    );
END arq;