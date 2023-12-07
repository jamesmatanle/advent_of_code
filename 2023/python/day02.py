"""https://adventofcode.com/2023/day/2"""
import re


def part1(A):
    return sum([int(re.findall(r'(\d+):', line)[0])
                for line in A
                if (13 > max([int(x) for x in re.findall(r'(\d+) red', line)])
                    and 14 > max([int(x) for x in re.findall(r'(\d+) green', line)])
                    and 15 > max([int(x) for x in re.findall(r'(\d+) blue', line)]))])


def part2(A):
    return sum([(max([int(x) for x in re.findall(r'(\d+) red', line)])
                 * max([int(x) for x in re.findall(r'(\d+) green', line)])
                 * max([int(x) for x in re.findall(r'(\d+) blue', line)]))
                for line in A])


TEST = """
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
""".strip().splitlines()

IN = open('day02_input.txt').readlines()

print('part1 test:', part1(TEST))  # 8
print('part1', part1(IN))

print('part2 test:', part2(TEST))  # 2286
print('part2:', part2(IN))
