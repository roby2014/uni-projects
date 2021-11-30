library ieee;
use ieee.STD_LOGIC_1164.all;

entity tlab1_tb is 
end tlab1_tb;

architecture teste of tlab1_tb is

component tlab1 port(
		A: in std_logic_vector(3 downto 0); 
		D: out std_logic_vector(3 downto 0)
);
end component;

signal A: std_logic_vector(3 downto 0);
SIGNAL D: std_logic_vector(3 downto 0);

begin

U0 : tlab1 port map (A => A, D => D);


process
begin
 A <= "0101";
 D(0) <= (not(A(1)) and A(2) and not(A(3))) or (not(A(3)) and A(0) and not(A(1)))
		or ( A(0) and A(1) and A(2)) or ( not(A(0)) and not(A(1)) and not(A(2)) and A(3)) or
		(not(A(0)) and A(1) and not(A(2)) and not(A(3)));
 
 wait for 10 ns;

 A <= "0111";
 D(0) <= (not(A(1)) and A(2) and not(A(3))) or (not(A(3)) and A(0) and not(A(1)))
		or ( A(0) and A(1) and A(2)) or ( not(A(0)) and not(A(1)) and not(A(2)) and A(3)) or
		(not(A(0)) and A(1) and not(A(2)) and not(A(3)));

wait;

end process;



end teste;
	