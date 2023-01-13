int i      = 0;
int i_last = 0; // 1 bit only (last bit 0 of i)

int main() {

    while (1) {
        i = input_port();
        i_last = i & 1;

        if (i_last == 0) {
            continue;
        }

        int low_bit = i & 1;
        if (low_bit == 1) {
            continue;
        }

        update_value();
    }
}

void update_value() {
    i_last = i & 1;
    int value = i & 0xFE;
    outport_write(value);
}