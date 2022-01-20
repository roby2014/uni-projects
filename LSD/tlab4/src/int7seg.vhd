LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY int7seg IS
PORT(	d0, d1, d2, d3: IN STD_LOGIC;
		dOut: out std_logic_vector(7 downto 0)
		);
END int7seg;

ARCHITECTURE logicFunction OF int7seg IS

signal Ndout : std_logic_vector(7 downto 0);

BEGIN

dout <= not Ndout;

Ndout(0) <= (d1 and d0) or (d2 and d0) or (d3 and not d1) or (not d3 and not d2 and not d0);
Ndout(1) <= (not d3 and not d2) or (not d2 and not d1) or (not d1 and not d0) or (d1 and d0);
Ndout(2) <= not d1 or d0 or d2;
Ndout(3) <= (not d0 and not d1 and not d2) or (not d3 and not d2 and d1) or (not d3 and d1 and not d0) or (d2 and not d1 and d0);
Ndout(4) <= (not d2 and not d1 and not d0) or (not d3 and d1 and not d0);
Ndout(5) <= (not d1 and not d0) or (d2 and not d1) or (d2 and not d0) or (d3 and not d1);
Ndout(6) <= d3 or (not d2 and d1) or (d1 and not d0) or (d2 and not d1);
Ndout(7) <= '0';


--process(d)
--begin
--case d is
--	when "0000" => Ndout <= "00111111";
--	when "0001" => Ndout <= "00000110";
--	when "0010" => Ndout <= "01011011";
--	when "0011" => Ndout <= "01001111";
--	when "0100" => Ndout <= "01100110";
--	when "0101" => Ndout <= "01101101";
--	when "0110" => Ndout <= "01111100";
--	when "0111" => Ndout <= "00000111";
--	when "1000" => Ndout <= "01111111";
--	when "1001" => Ndout <= "01100111";
--	when others => Ndout <= "01000000";
--end case;
--end process;		

END LogicFunction;