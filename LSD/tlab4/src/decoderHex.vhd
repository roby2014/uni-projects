LIBRARY IEEE;
use IEEE.std_logic_1164.all;

entity decoderHex IS
PORT (A, B: in std_logic_vector(3 downto 0);		
		P : in std_logic_vector(6 downto 0);
		clear : in std_logic;
		HEX0, HEX1, HEX2, HEX3, HEX4, HEX5 : out std_logic_vector(7 downto 0));		
END decoderHex;

Architecture logicFuntion of decoderHex is

component int7seg
PORT(	d0, d1, d2, d3: IN STD_LOGIC;
		dOut: out std_logic_vector(7 downto 0)
		);
END component;

component bin2dec_int
    Port ( bin : in  STD_LOGIC_VECTOR (7 downto 0);
           dec : out  STD_LOGIC_VECTOR (7 downto 0));
end component;

SIGNAL a0s, a1s, a0u, a1u : std_logic_vector(3 downto 0);
signal a0, a1, a2, a3 : std_logic;
signal b0, b1, b2, b3 : std_logic;
signal times : std_logic_vector(3 downto 0);
signal Pdec, HEX1t, HEX0t: std_logic_vector(7 downto 0);
signal bin : STD_LOGIC_VECTOR (7 downto 0);

BEGIN

a0 <= a(0); a1 <= a(1); a2 <= a(2); a3 <= a(3);
b0 <= b(0); b1 <= b(1); b2 <= b(2); b3 <= b(3);

a0u(3) <= a3 and not a2 and not a1;
a0u(2) <= (not a3 and a2) or (a2 and a1);
a0u(1) <= (not a3 and a1) or (a3 and a2 and not a1);
a0u(0) <= a0;

a0s(3) <= b3 and not b2 and not b1;
a0s(2) <= (not b3 and b2) or (b2 and b1);
a0s(1) <= (not b3 and b1) or (b3 and b2 and not b1);
a0s(0) <= b0;


U5: int7seg port map(a0u(0), a0u(1), a0u(2), a0u(3), HEX5);
HEX4 <= "01111111";
U3: int7seg port map(a0s(0), a0s(1), a0s(2), a0s(3), HEX3);
HEX2 <= "10110111";

bin <= '0' & P;
Ub : bin2dec_int port map(bin, Pdec);

HEX1 <= HEX1t when clear = '0' else "11111111";
HEX0 <= HEX0t when clear = '0' else "11111111";

U2: int7seg port map(Pdec(4), Pdec(5), Pdec(6), Pdec(7), HEX1t);
U1: int7seg port map(Pdec(0), Pdec(1), Pdec(2), Pdec(3), HEX0t);
									
END logicFuntion;	