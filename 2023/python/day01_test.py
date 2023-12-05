from day01 import part1, part2

IN = open('day01_input.txt').readlines()


TEST = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
""".strip().splitlines()

print('part1 test:', part1(TEST))
print('part1:', part1(IN))

TEST = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
""".strip().splitlines()

print('part2 test:', part2(TEST))
print('part2:', part2(IN))
