import re
# import util


def part1(S):
    # @util.viz
    def next_change(A):
        if all(0 == x for x in A):
            return A[-1]
        return A[-1] + next_change([A[i] - A[i-1] for i in range(1, len(A))])
    return sum(
        next_change(line) for line in
        [[int(x) for x in re.findall(r'-?\d+', A)] for A in S.splitlines()])


def part2(S):
    # @util.viz
    def next_change(A):
        if all(0 == x for x in A):
            return A[0]
        return A[0] - next_change([A[i] - A[i-1] for i in range(1, len(A))])
    return sum(
        next_change(line) for line in
        [[int(x) for x in re.findall(r'-?\d+', A)] for A in S.splitlines()])


TEST = """
0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
""".strip()

IN = open('day09_input.txt').read()

print('part1 test:', part1(TEST))  # => 114
print('part1:', part1(IN))

print('part2 test:', part2(TEST))  # => 2
print('part2:', part2(IN))
