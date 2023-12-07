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
