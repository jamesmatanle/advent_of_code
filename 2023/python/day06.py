import math
import re


def part1(A):
    def num_ways(t, d):
        return sum(1 for x in range(t) if d < (x*(t-x)))
    A = zip(*[re.findall(r'\d+', line) for line in A])
    return math.prod(num_ways(int(t), int(d)) for t, d in A)


# distance is parabolic. use binary search to find first point above d
def part2(A):
    t, d = [int(x) for line in A for x in re.findall(r'\d+', line.replace(' ', ''))]
    lo, hi = 0, t // 2
    while lo < hi:
        mid = (lo + hi) // 2
        if (mid * (t - mid)) < d:
            lo = mid + 1
        else:
            hi = mid
    return t - lo - lo + 1


IN = open('day06_input.txt').read().splitlines()

TEST = """
Time:      7  15   30
Distance:  9  40  200
""".strip().splitlines()

print('part1 test:', part1(TEST))  # 288
print('part1:', part1(IN))

print('part2 test:', part2(TEST))  # 71503
print('part2:', part2(IN))
