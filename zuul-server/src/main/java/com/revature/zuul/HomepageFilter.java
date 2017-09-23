package com.revature.zuul;

import com.netflix.zuul.ZuulFilter;

public class HomepageFilter extends ZuulFilter{

	@Override
	public Object run() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public int filterOrder() {
		return -50;
	}

	@Override
	public String filterType() {
		return "pre";
	}

}
