library ieee;
use ieee.STD_LOGIC_1164.all;


entity logic_mux is 
	port(
		Mand: in std_logic_vector(3 downto 0);
		Mor: in std_logic_vector(3 downto 0);
		Mxor: in std_logic_vector(3 downto 0);
		Mnot: in std_logic_vector(3 downto 0);

		OP: in std_logic_vector(1 downto 0);
		
		S: out std_logic_vector(3 downto 0)
	);
end logic_mux;


architecture arq of logic_mux is
begin
S(0) <= ( OP(0) and Mand(0)) or (not OP(0) and Mor(0)) or ( OP(1) and Mxor(0)) or (not OP(1) and Mnot(0));
S(1) <= ( OP(0) and Mand(1)) or (not OP(0) and Mor(1)) or ( OP(1) and Mxor(1)) or (not OP(1) and Mnot(1));
S(2) <= ( OP(0) and Mand(2)) or (not OP(0) and Mor(2)) or ( OP(1) and Mxor(2)) or (not OP(1) and Mnot(2));
S(3) <= ( OP(0) and Mand(3)) or (not OP(0) and Mor(3)) or ( OP(1) and Mxor(3)) or (not OP(1) and Mnot(3));
end arq;