-- testbench

LIBRARY IEEE;
use IEEE.std_logic_1164.all;

entity TLAB4_tb is
end TLAB4_tb;

architecture teste of TLAB4_tb is
component ALU
  port(W, Y : in STD_LOGIC_VECTOR(3 downto 0);
	   OP : in STD_LOGIC_vector(2 downto 0);
	   F : out STD_LOGIC_vector(3 downto 0);
	   CyBw, OV, Z, P, GE : out STD_LOGIC
  );
end component;

signal W, Y : std_logic_vector(3 downto 0);
signal OP : std_logic_vector(2 downto 0);
signal F : std_logic_vector(3 downto 0);
signal CyBw, OV, Z, P, GE : std_logic;

begin

U0 : ALU port map (
	W => W, 
	Y => Y, 
	OP => OP, 
	F => F, 
	CyBw => CyBw, 
	OV => OV, 
	Z => Z, 
	P => P, 
	GE => GE
);

process

-- dados da tabela pdf enunciado
begin
 W <= "1010"; 
 Y <= "0101"; 
 OP <= "100";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1101"; 
 OP <= "100";
 wait for 10 ns;

 W <= "0110"; 
 Y <= "0101"; 
 OP <= "100";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1010"; 
 OP <= "100";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "0101"; 
 OP <= "101";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1101"; 
 OP <= "101";
 wait for 10 ns;

 W <= "0110"; 
 Y <= "0101"; 
 OP <= "101";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1010"; 
 OP <= "101";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "0101"; 
 OP <= "110";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1101"; 
 OP <= "110";
 wait for 10 ns;

 W <= "0110"; 
 Y <= "0101"; 
 OP <= "110";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1010"; 
 OP <= "110";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "0101"; 
 OP <= "111";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1101"; 
 OP <= "111";
 wait for 10 ns;

 W <= "0110"; 
 Y <= "0101"; 
 OP <= "111";
 wait for 10 ns;

 W <= "1010"; 
 Y <= "1010"; 
 OP <= "111";
 wait for 10 ns;

 W <= "1011"; 
 Y <= "1101"; 
 OP <= "000";
 wait for 10 ns;

 W <= "1011"; 
 Y <= "1101"; 
 OP <= "001";
 wait for 10 ns;

 W <= "1011"; 
 Y <= "1101"; 
 OP <= "010";
 wait for 10 ns;

 W <= "1011"; 
 Y <= "1101"; 
 OP <= "011";


wait;

end process;

end teste;
