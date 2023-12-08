import math


def part1(A):
    def num_ways(t, d):
        return sum(1 for x in range(t) if d < (x*(t-x)))
    return math.prod(num_ways(t, d) for t, d in A)


# distance is parabolic. use binary search to find first point above d
def part2(t, d):
    lo, hi = 0, t // 2
    while lo < hi:
        mid = (lo + hi) // 2
        if (mid * (t - mid)) < d:
            lo = mid + 1
        else:
            hi = mid
    return t - lo - lo + 1


TEST1 = [[7, 9], [15, 40], [30, 200]]
IN1 = [[56, 499], [97, 2210], [77, 1097], [93, 1440]]

print('part1 test:', part1(TEST1))  # 288
print('part1:', part1(IN1))

TEST2 = [71530, 940200]
IN2 = [56977793, 499221010971440]

print('part2 test:', part2(*TEST2))  # 71503
print('part2:', part2(*IN2))
