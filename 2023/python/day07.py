import collections

card_values = {c: i for i, c in enumerate('23456789TJQKA')}

hand_values = {c: i for i, c in enumerate(
    [(1, 1, 1, 1, 1), (1, 1, 1, 2), (1, 2, 2), (1, 1, 3), (2, 3), (1, 4), (5,)])}


def hand_value(hand):
    return hand_values[tuple(sorted(collections.Counter(hand).values()))]


# parse the input into a format that easily compares hand value
def part1(A):
    A = [x.split() for x in A]
    A = [(tuple([hand_value(hand)] + [card_values[c] for c in hand]),
          int(bid))
         for hand, bid in A]
    return sum([bid*(i+1) for i, (_, bid) in enumerate(sorted(A))])


def part2(A):
    return


TEST = """
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483
""".strip().splitlines()

IN = open('day07_input.txt').read().splitlines()

print('part1 test:', part1(TEST))  #
print('part1:', part1(IN))

print('part2 test:', part2(TEST))  #
print('part2:', part2(IN))
