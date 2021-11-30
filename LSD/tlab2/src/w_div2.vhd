library ieee;
use ieee.STD_LOGIC_1164.all;


entity w_div2 is 
	port(
		W: in std_logic_vector(3 downto 0);
		OP: in std_logic;
		
		A: out std_logic_vector(3 downto 0)
	);
end w_div2;


architecture arq_div of w_div2 is
begin
	A(0) <= (W(0) and not(OP)) or (W(1) and OP);
	A(1) <= (W(1) and not(OP)) or (W(2) and OP);
	A(2) <= (W(2) and not(OP)) or (W(3) and OP);
	A(3) <= W(3);
end arq_div;
