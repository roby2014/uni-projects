LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY controlador_registo IS
    PORT (
        D : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
        CLK, Reset : IN STD_LOGIC;
        Q : OUT STD_LOGIC_VECTOR(1 DOWNTO 0)
    );
END controlador_registo;

ARCHITECTURE arq OF controlador_registo IS

    COMPONENT FFD
        PORT (
            CLK : IN STD_LOGIC;
            RESET : IN STD_LOGIC;
            SET : IN STD_LOGIC;
            D : IN STD_LOGIC;
            EN : IN STD_LOGIC;
            Q : OUT STD_LOGIC
        );
    END COMPONENT;

BEGIN

    u_ffd0 : FFD PORT MAP(
        D => D(0),
        Q => Q(0),
        RESET => Reset,
        SET => '0',
        CLK => CLK,
        EN => '1'
    );

    u_ffd1 : FFD PORT MAP(
        D => D(1),
        Q => Q(1),
        RESET => Reset,
        SET => '0',
        CLK => CLK,
        EN => '1'
    );

END arq;