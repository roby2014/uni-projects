LIBRARY IEEE;
use IEEE.std_logic_1164.all;

entity LAB4_tb is
end LAB4_tb;

architecture teste of LAB4_tb is
component tlab4
  port(A, B : in STD_LOGIC_VECTOR(3 downto 0);
	   OP : in STD_LOGIC;
	   R : out STD_LOGIC_VECTOR(3 downto 0);
	   CyBw, OV : out STD_LOGIC
  );
end component;

signal A, B : std_logic_vector(3 downto 0);
signal OP : std_logic;
signal R : std_logic_vector(3 downto 0);
signal CyBw, OV : std_logic;

begin

U0 : tlab4 port map (
			A => A, 
			B => B, 
			OP => OP, 
			R => R, 
			CyBw => CyBw, 
			OV => OV
		);

process
begin
 A <= "0101"; 
 B <= "1101"; 
 OP <= '0';
 wait for 10 ns;

 A <= "0101"; 
 B <= "1101"; 
 OP <= '1';
 wait for 10 ns;

wait;

end process;

end teste;
