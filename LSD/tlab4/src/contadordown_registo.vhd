LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY contadordown_registo IS
    PORT (
        CLK, CE, reset: IN STD_LOGIC;
        M : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
    );
END contadordown_registo;

ARCHITECTURE arq OF contadordown_registo IS

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

    UFFD0 : FFD PORT MAP(
        EN => CE,
        RESET => reset,
        SET => '0',
        CLK => CLK,
        D => M(0),
        Q => Q(0));

    UFFD1 : FFD PORT MAP(
        EN => CE,
        RESET => reset,
        SET => '0',
        CLK => CLK,
        D => M(1),
        Q => Q(1));

    UFFD2 : FFD PORT MAP(
        EN => CE,
        RESET => reset,
        SET => '0',
        CLK => CLK,
        D => M(2),
        Q => Q(2));

    UFFD3 : FFD PORT MAP(
        EN => CE,
        RESET => reset,
        SET => '0',
        CLK => CLK,
        D => M(3),
        Q => Q(3));

END arq;