-- reune os flip flops (ver pdfs)

LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY registo IS
    PORT (
        F : IN STD_LOGIC_VECTOR(3 DOWNTO 0); -- vem do mux
        CE : IN STD_LOGIC;
        CLK : IN STD_LOGIC;
        Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
    );
END registo;

ARCHITECTURE arq OF registo IS

    COMPONENT FFD
        PORT (
            CLK, D, EN, SET, RESET : IN STD_LOGIC;
            Q : OUT STD_LOGIC
        );
    END COMPONENT;

BEGIN

    -- flip flop 0
    u_FFD0 : FFD PORT MAP(
        D => F(0),
        EN => CE,
        CLK => CLK,
        Q => Q(0),
        SET => '0',
        RESET => '0'
    );

    -- flip flop 1
    u_FFD1 : FFD PORT MAP(
        D => F(1),
        EN => CE,
        CLK => CLK,
        Q => Q(1),
        SET => '0',
        RESET => '0'
    );

    -- flip flop 2
    u_FFD2 : FFD PORT MAP(
        D => F(2),
        EN => CE,
        CLK => CLK,
        Q => Q(2),
        SET => '0',
        RESET => '0'
    );

    -- flip flop 3
    u_FFD3 : FFD PORT MAP(
        D => F(3),
        EN => CE,
        CLK => CLK,
        Q => Q(3),
        SET => '0',
        RESET => '0'
    );

END arq;