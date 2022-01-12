 -- maq de estados
 
LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY mde IS
    PORT (
		reset : IN std_logic;
		B : IN STD_LOGIC;

		E, A, V, EP, VP : out std_logic;		
		PL : OUT STD_LOGIC;
      CE : OUT STD_LOGIC;
      S : OUT STD_LOGIC;
		TC : IN STD_LOGIC;
		CLK : in std_logic;
      clear : OUT STD_LOGIC
    );
END mde;

ARCHITECTURE arq OF mde IS

COMPONENT FFD
        PORT (
            CLK, D, EN, SET, RESET : IN STD_LOGIC;
            Q : OUT STD_LOGIC
        );
    END COMPONENT;

SIGNAL s_D0, s_D1, s_Q0, s_Q1 : STD_LOGIC; -- sinais para prox estado com ffs
	 
BEGIN

u_FFD0 : FFD PORT MAP(
        D => s_D0,
        EN => '1',
        CLK => CLK,
        Q => s_Q0,
        SET => '0',
        RESET => reset
    );
	 
u_FFD1 : FFD PORT MAP(
        D => s_D1,
        EN => '1',
        CLK => CLK,
        Q => s_Q1,
        SET => '0',
        RESET => reset
    );

V <= NOT(s_Q1) AND NOT(s_Q0);
EP <= (NOT(s_Q1) AND NOT(s_Q0)) OR (NOT(s_Q1) AND s_Q0) OR (s_Q1 AND NOT(s_Q0));
PL <= (NOT(s_Q1) AND NOT(s_Q0)) OR (s_Q1 AND NOT(s_Q0));
clear <= NOT(s_Q1) AND NOT(s_Q0);
CE <= '1';--(NOT(s_Q1) AND NOT(s_Q0)) OR (NOT(s_Q1) AND s_Q0) OR (s_Q1 AND NOT(s_Q0)) OR (s_Q1 AND s_Q0);
A <= NOT(s_Q1) AND s_Q0;
S <= s_Q1 AND NOT(s_Q0);
VP <= s_Q1 and s_Q0; -- not q0
E <= (s_Q1 and NOT(s_Q0)) or (s_Q1 and S_Q0);

s_D0 <= (NOT(s_Q1) and NOT(s_Q0) and B) or 
	(NOT(s_Q1) and s_Q0 and NOT(TC)) or 
	(s_Q1 and NOT(s_Q0) and NOT(TC)) or 
	(s_Q1 and s_Q0 and NOT(TC));

s_D1 <= (NOT(s_Q1) AND s_Q0 AND TC) OR
	(s_Q1 AND NOT(s_Q0) AND TC) OR
	(s_Q1 AND NOT(s_Q0) AND NOT(TC)) OR
	(s_Q1 AND s_Q0 AND NOT(TC));


END arq;