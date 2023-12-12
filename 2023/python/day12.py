import collections
import itertools
import math
import re
import util


def part1(A):
    # @util.viz
    def fn(B, C, breakable):
        if not C:
            return 1
        if len(B) < C[0]:
            return 0
        if B[0] == '#':  # must break
            if not breakable:
                return 0
            if not all(B[k] in '?#' for k in range(C[0])):
                return 0
            return fn(B[C[0]:], C[1:], False)
        if B[0] == '?':
            if breakable and all(B[k] in '?#' for k in range(C[0])):
                return (fn(B[C[0]:], C[1:], False) + fn(B[1:], C, True))
        return fn(B[1:], C, True)
    return sum(fn(tuple(line.split()[0]),
                  tuple([int(x) for x in line.split()[1].split(',')]),
                  True)
               for line in A)


def part2(S):
    return


TEST = """
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
""".strip().splitlines()

IN = open('day12_input.txt').read().splitlines()

print('part1 test:', part1(TEST))  # =>
print('part1:', part1(IN))

# print('part2 test:', part2(TEST))  # =>
# print('part2:', part2(IN))
