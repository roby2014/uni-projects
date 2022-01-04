-- entidade topo, faz as ligações todas (o contador em si basicamente)

LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;

ENTITY lab5 IS
    PORT (
        Data_in : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        PL : IN STD_LOGIC;
        CE : IN STD_LOGIC;
        CLK : IN STD_LOGIC;
        clear : IN STD_LOGIC;

        SEG7 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
        Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
        TC : OUT STD_LOGIC
    );
END lab5;

ARCHITECTURE arq OF lab5 IS

    -- do stor
    COMPONENT CLKDIV
        GENERIC (div : NATURAL := 50000000);
        PORT (
            clk_in : IN STD_LOGIC;
            clk_out : OUT STD_LOGIC
        );
    END COMPONENT;

    COMPONENT decoderHex
        PORT (
            A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            clear : IN STD_LOGIC;
            HEX0 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0)
        );

    END COMPONENT;

    -- os nossos
    COMPONENT mux
        PORT (
            A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            PL : IN STD_LOGIC; -- seletor parallel load
            F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
        );
    END COMPONENT;

    COMPONENT registo
        PORT (
            F : IN STD_LOGIC_VECTOR(3 DOWNTO 0); -- vem do mux
            CE : IN STD_LOGIC;
            CLK : IN STD_LOGIC;
            Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
        );
    END COMPONENT;

    COMPONENT adder
        PORT (
            A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
            Cin : IN STD_LOGIC;

            S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
            Cout : OUT STD_LOGIC
        );
    END COMPONENT;

    SIGNAL s_clk : STD_LOGIC;
    SIGNAL s_mux, s_add, s_reg : STD_LOGIC_VECTOR(3 DOWNTO 0);

BEGIN
    u_CLKDIV : CLKDIV PORT MAP(
        clk_in => CLK,
        clk_out => s_clk
    );

    u_decoderHex : decoderHex PORT MAP(
        A => s_reg,
        clear => clear,
        HEX0 => SEG7
    );

    u_mux : mux PORT MAP(
        A => Data_in,
        B => s_add,
        PL => PL,
        F => s_mux
    );

    u_registo : registo PORT MAP(
        F => s_mux,
        CLK => s_clk,
        CE => CE,
        Q => s_reg
    );

    u_adder : adder PORT MAP(
        A => s_reg,
        B => "1111", -- 15
        Cin => '0',
        Cout => OPEN,
        S => s_add
    );

    Q <= s_reg;
    TC <= NOT(s_reg(0)) AND NOT(s_reg(1)) AND NOT(s_reg(2)) AND NOT(s_reg(3));
END arq;