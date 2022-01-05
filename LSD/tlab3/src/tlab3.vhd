-- entidade topo

LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY tlab3 IS
	PORT (
		clk : IN STD_LOGIC;
		reset : IN STD_LOGIC;
		B : IN STD_LOGIC;
		Tempo : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		E, A, V, EP, VP : OUT STD_LOGIC;
		HEX0 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
		Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END tlab3;

ARCHITECTURE arq OF tlab3 IS

	COMPONENT semaforo_mux
		PORT (
			Tempo : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
			S : IN STD_LOGIC;
			F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT mde
		PORT (
			reset : IN STD_LOGIC;
			B : IN STD_LOGIC;

			E, A, V, EP, VP : OUT STD_LOGIC;
			PL : OUT STD_LOGIC;
			CE : OUT STD_LOGIC;
			S : OUT STD_LOGIC;
			TC : IN STD_LOGIC;
			clear : OUT STD_LOGIC;
			CLK : IN STD_LOGIC
		);
	END COMPONENT;

	COMPONENT lab5
		PORT (
			Data_in : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
			PL : IN STD_LOGIC;
			CE : IN STD_LOGIC;
			CLK : IN STD_LOGIC;
			clear : IN STD_LOGIC;

			SEG7 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
			Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
			TC : OUT STD_LOGIC
		);
	END COMPONENT;

	SIGNAL s_s : STD_LOGIC; -- mde -> mux (S)
	SIGNAL s_f : STD_LOGIC_VECTOR(3 DOWNTO 0); -- mux -> lab5 (F)
	SIGNAL s_pl, s_ce, s_clear : STD_LOGIC; -- mde -> lab5 ()
	SIGNAL s_tc : STD_LOGIC; -- lab5 -> mde

BEGIN

	u_semaforo_mux : semaforo_mux PORT MAP(
		Tempo => Tempo,
		S => s_s,
		F => s_f
	);

	u_mde : mde PORT MAP(
		reset => reset,
		B => B,
		E => E,
		A => A,
		V => V,
		EP => EP,
		VP => VP,
		PL => s_pl,
		CE => s_ce,
		clear => s_clear,
		S => s_s,
		TC => s_tc,
		CLK => clk
	);

	u_lab5 : lab5 PORT MAP(
		Data_in => s_f,
		PL => s_pl,
		CE => s_ce,
		CLK => clk,
		clear => s_clear,
		SEG7 => HEX0,
		Q => Q,
		TC => s_tc
	);
END arq;