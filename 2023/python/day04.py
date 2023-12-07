import collections
import re


def num_matches(s):
    return len(set(int(x) for x in re.findall(r'\d+', s.split('|')[0])[1:])
               & set(int(x) for x in re.findall(r'\d+', s.split('|')[1])))


def part1(A):
    def score(x):
        return 2**(x-1) if x > 0 else 0
    return sum(score(num_matches(s)) for s in A)


def part2(A):
    cnt = collections.Counter(range(len(A)))
    for i in range(len(A)):
        for j in range(num_matches(A[i])):
            cnt[i+j+1] += cnt[i]
    return sum(cnt.values())


TEST = """
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
""".strip().splitlines()

IN = open('day04_input.txt').read().splitlines()

print('part1 test:', part1(TEST))  # 13
print('part1:', part1(IN))

print('part2 test:', part2(TEST))  # 30
print('part2:', part2(IN))
