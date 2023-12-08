import math
import re
import itertools


def parse(S):
    instructions, graph = S.split('\n\n')
    graph = {line.split(' = ')[0]: re.findall(r'\w+', line.split(' = ')[1])
             for line in graph.splitlines()}
    return instructions, graph


def part1(S):
    instructions, graph = parse(S)
    pos = 'AAA'
    cnt = 0
    for instr in itertools.cycle(instructions):
        if pos == 'ZZZ':
            return cnt
        pos = graph[pos][0 if instr == 'L' else 1]
        cnt += 1


# part 2 - brute force is too slow, instead find LCM of all cycles
def part2(S):
    def path_length(x):
        cnt = 0
        for instr in itertools.cycle(instructions):
            if x[-1] == 'Z':
                return cnt
            x = graph[x][0 if instr == 'L' else 1]
            cnt += 1
    instructions, graph = parse(S)
    return math.lcm(*[path_length(x) for x in graph if x[-1] == 'A'])


TEST = """
RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)
""".strip()

IN = open('day08_input.txt').read()

print('part1 test:', part1(TEST))  # 2
print('part1:', part1(IN))

TEST = """
LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)
""".strip()

print('part2 test:', part2(TEST))  # 6
print('part2:', part2(IN))
