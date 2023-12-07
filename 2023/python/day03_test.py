from day03 import part1, part2

TEST = """
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..
""".strip().splitlines()


IN = open('day03_input.txt').read().splitlines()

print(f'part1 test: {part1(TEST)}')  # 4361
print(f'part1: {part1(IN)}')

print(f'part2 test: {part2(TEST)}')
print(f'part2: {part2(IN)}')
