export interface Menu {
	id: string
	title: string
	link: string
}


export const navMenus: Menu[] = [
	{
		id: 'dashboard',
		title: 'Dashboard',
		link: '/dashboard'
	},
	{
		id: 'team',
		title: 'Team',
		link: '#team'
	},
	{
		id: 'roadmap',
		title: 'Roadmap',
		link: '#roadmap'
	},
	{
		id: 'contact',
		title: 'Contact',
		link: '#contact'
	}
]
