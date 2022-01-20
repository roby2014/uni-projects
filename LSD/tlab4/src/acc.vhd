-- registo 7 bits 

LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY acc IS
    PORT (
        EA, CLK, Rst : IN STD_LOGIC;
        D : IN STD_LOGIC_VECTOR(6 DOWNTO 0);
        Q : OUT STD_LOGIC_VECTOR(6 DOWNTO 0)
    );
END acc;

ARCHITECTURE arq OF acc IS

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
        EN => EA,
        RESET => Rst,
        CLK => CLK,
        D => D(0),
        Q => Q(0),
        SET => '0'
    );

    u_ffd1 : FFD PORT MAP(
        EN => EA,
        RESET => Rst,
        CLK => CLK,
        D => D(1),
        Q => Q(1),
        SET => '0'
    );

    u_ffd2 : FFD PORT MAP(
        EN => EA,
        RESET => Rst,
        CLK => CLK,
        D => D(2),
        Q => Q(2),
        SET => '0'
	);

    u_ffd3 : FFD PORT MAP(
        EN => EA,
        RESET => Rst,
        CLK => CLK,
        D => D(3),
        Q => Q(3),
        SET => '0'
    );

    u_ffd4 : FFD PORT MAP(
        EN => EA,
        RESET => Rst,
        CLK => CLK,
        D => D(4),
        Q => Q(4),
        SET => '0'
    );

    u_ffd5 : FFD PORT MAP(
        EN => EA,
        RESET => Rst,
        CLK => CLK,
        D => D(5),
        Q => Q(5),
        SET => '0'
    );

    u_ffd6 : FFD PORT MAP(
        EN => EA,
        RESET => Rst,
        CLK => CLK,
        D => D(6),
        Q => Q(6),
        SET => '0'
    );

END arq;