#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>

#define SYSCLK_FREQ 0x31 //  1 KHz = 49 ms
#define LEVEL_ONE_TIME 16  // 50ms * 16 = 800 ms
#define LEVEL_TWO_TIME 8   // 50ms * 8  = 400 ms 
#define LEVEL_THREE_TIME 4 // 50ms * 4  = 200 ms

#define AFTER_POINTS_NOTIFY 5    // 50ms * 5 = 250ms
#define AFTER_POINTS_DISMISS 15 // 50ms * 15 = 750ms
#define AFTER_ONE_SEC 10    // 50ms * 20 = 1s
#define DELAY_GAME_OVER 0x64    // 50ms * 100 = approx 5s

#define WALL_POS 6
#define NEAR_PLAYER 1

uint8_t sm_state; // state machine state

const uint8_t ball_pos_array[] = { 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02 };
uint8_t ball_pos; // ball position index

uint16_t points; // score
bool ball_dir; // ball direction, 1 = forwards, 0 = backwards
uint8_t level; // level (00, 01, 10, 11)
uint16_t level_time; // level time ()

const uint8_t speed_array[] = { LEVEL_ONE_TIME,LEVEL_TWO_TIME, LEVEL_THREE_TIME, 0x2, 0x1 };
uint8_t speed_pos;

uint16_t old_timer;
uint16_t score_timer; 

int main() {
    // outport_init(OUTPORT_INIT_VAL);
    // ptc_init(SYSCLK_FREQ);
    // setup cpsr
    // ..?

state_machine:
    switch (sm_state)
    {
    case 0:
        new_game();
        break;
    case 1:
        playing();
        break;
    case 2:
        // game_over();
        break;
    default:
        goto state_machine; // PERGUNTA: Caso newgame ou playing retorne e mude o estado de sm_state, como voltamos a executar o switch ?
    }
}

/**
STATE 1
**/

void new_game() {
    game_setup();
    while (true) {
        bool stroke_valid = racket_test_stroke();
        if (stroke_valid) {
            break;
        }
    }
    level_get_value();
    sm_state = 1; // PLAYING
}

void game_setup() {
    points = 0;
    ball_pos = 0;
    // outport_write(ball_pos_array[ball_pos])
    ball_dir = true;
    speed_pos = 0;
}

bool racket_test_stroke() {
    uint8_t first_instance = inport_read() & 0x1; // mask I0
    uint8_t second_instance  = inport_read() & 0x1; // mask I0
    return (first_instance < second_instance);

}

void level_get_value() {
    level = inport_read() & 0xC0; // mask I7 I6
    switch (level)
    {
    case 0: // slow
        level_time = LEVEL_ONE_TIME;
        break;
    case 0x40: // normal
        level_time = LEVEL_TWO_TIME;
        break;
    case 0x80: // fast
        level_time = LEVEL_THREE_TIME;
        break;
    case 0xC0: // mixed
        speed_pos = 0;
        level_time = LEVEL_ONE_TIME; // start at 0
        break;
    default:
        break;
    }
}

/**
STATE 2
**/

void playing() {
    score_timer = old_timer = sysclk_get_ticks();
    ball_move();
    while (true) { // ou while sm_state != GAME_OVER ?
        // after(level_time)
        if (elapsed(old_timer) > level_time) {
            if (ball_pos == WALL_POS) {
                old_timer = sysclk_get_ticks();
                ball_inv_movment();
                continue;
            }
            if (ball_pos != NEAR_PLAYER && ball_pos != WALL_POS) {
                // move ball
                old_timer = sysclk_get_ticks();
                continue;
            }
            if (ball_pos == NEAR_PLAYER && ball_dir == 0 /*backwards*/) {
                // ball_move();
                bool game_not_over = wait_for_player_move();
                if (!game_not_over) {
                    sm_state = 2; // GAME_OVER;
                    break;
                } else {
                    // PERGUNTA: aqui chamamos playing() ou damos continue?
                    // se chamarmos playing(), não vamos entrar em recursao infinita?
                    // se dermos so continue, ball_move não é chamado porque so executa este loop principal de novo
                }
            }
        }

        /****** POINTS STAND BY */
        // PERGUNTA: after(1s) ou 750ms ??
        if (elapsed(score_timer) > 750/*ms*/) { 
            points_notify();
        }

        // after(250ms)
        if (elapsed(score_timer) > 250/*ms*/) { 
            points_dismiss();
        }
    }
}

// 1 = sucess, 0 = false
bool wait_for_player_move() {
    old_timer = sysclk_get_ticks();
    while (true) {
        bool stroke_valid = racket_test_stroke();
        if (stroke_valid) {
            // ball_inv_movment();
            return true;
        }

        if (elapsed(old_timer) > level_time) {
            return false;
        }
    }
}

void points_notify() {
    points++;
    // points_led_on();
    score_timer = sysclk_get_ticks();
}

void points_dismiss() {
    // points_led_off();
    score_timer = sysclk_get_ticks();
}

uint16_t elapsed(uint16_t t0) {
    return (sysclk_get_tick() - t0);
}