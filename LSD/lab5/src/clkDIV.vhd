LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;
USE IEEE.numeric_std.ALL;

ENTITY CLKDIV IS
	GENERIC (div : NATURAL := 50000000);
	PORT (
		clk_in : IN STD_LOGIC;
		clk_out : OUT STD_LOGIC);
END CLKDIV;

ARCHITECTURE bhv OF CLKDIV IS

	SIGNAL count : INTEGER := 1;
	SIGNAL tmp : STD_LOGIC := '0';

BEGIN

	PROCESS (clk_in)
	BEGIN

		IF (clk_in'event AND clk_in = '1') THEN
			count <= count + 1;
			IF (count = div/2) THEN
				tmp <= NOT tmp;
				count <= 1;
			END IF;
		END IF;
	END PROCESS;

	clk_out <= tmp;

END bhv;